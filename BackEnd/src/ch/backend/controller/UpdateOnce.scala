package ch.backend.controller

import utopia.flow.util.CollectionExtensions._
import utopia.flow.time.TimeExtensions._
import utopia.flow.async.AsyncExtensions._
import ch.controller.Profiler
import ch.granite.controller.ReadGraniteData
import ch.mailchimp.controller.{UpdateContacts, UpdateSegments}
import ch.mailchimp.model.APIConfiguration
import ch.util.Log
import utopia.vault.database.Connection

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success, Try}

/**
  * Used for
  * 1) Reading Granite data
  * 2) Profiling companies and
  * 3) Updating data to MailChimp
  * @author Mikko Hilpinen
  * @since 20.7.2019, v0.1+
  */
object UpdateOnce
{
	/**
	  * Performs a complete update, reading data from Granite and possibly updating it to MailChimp
	  * @param exc Execution context
	  * @param connection DB connection
	  * @return Operation results. On Success contains asynchronous post results. Contains failure if settings were
	  *         not read successfully or if granite data read fails
	  */
	def apply()(implicit exc: ExecutionContext, connection: Connection) =
	{
		// Makes sure settings are OK
		APIConfiguration.fromSettings.flatMap { implicit mailChimpAPIConf =>
			
			// Starts by reading new data from Granite
			ReadGraniteData.synchronously().map { graniteReadResult =>
			
				// If no new responses were received, won't need to perform the rest of the updates
				if (graniteReadResult > 0)
				{
					// Performs company profiling next
					Profiler.run()
					
					// Handles each list separately, asynchronously
					ch.mailchimp.database.model.ContactList.getAll().map { list =>
						
						Future
						{
							// Updates contact data to mailChimp, waits for all updates to complete before next phase
							val updateResults = UpdateContacts(list).future.waitFor(180.seconds)
							logWarnings(updateResults, "MailChimp contact updates")
							
							// Updates segment data for the list
							val segmentUpdateResults = UpdateSegments(list).future.waitFor(180.seconds)
							logWarnings(segmentUpdateResults, "MailChimp segment updates")
						}
					}
				}
				else
					Vector()
			}
		}
	}
	
	private def logWarnings(updateResult: Try[Traversable[Try[_]]], operationName: String) =
	{
		updateResult match
		{
			case Success(results) =>
				if (results.exists { _.isFailure })
				{
					val failures = results.flatMap { _.failure }
					Log.warning(s"${failures.size}/${results.size} $operationName failed")
					failures.foreach { Log(_) }
				}
			
			case Failure(error) => Log(error, s"$operationName didn't complete successfully")
		}
	}
}
