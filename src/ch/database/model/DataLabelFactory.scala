package ch.database.model

import ch.model.DataType
import ch.model.DataType.StringType
import ch.util.Log
import utopia.flow.datastructure.immutable
import utopia.flow.datastructure.immutable.Constant
import utopia.flow.datastructure.template.{Model, Property}
import utopia.vault.model.immutable.factory.StorableFactory

import scala.util.{Failure, Success}

/**
  * This factory is used for reading / parsing data labels from DB
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  */
@deprecated("Replaced with EntityLabel", "v2")
trait DataLabelFactory[+M] extends StorableFactory[ch.model.DataLabel]
{
	// ABSTRACT	-------------------------
	
	/**
	  * Creates a new label model
	  * @param id Label id
	  * @param dataType Label data type
	  * @param isIdentifier Whether label should be identifier
	  * @return A new label db model
	  */
	protected def apply(id: Option[Int] = None, dataType: Option[DataType] = None, isIdentifier: Option[Boolean] = None,
						isEmail: Option[Boolean] = None): M
	
	
	// COMPUTED	---------------------
	
	/**
	  * A data label model where isIdentifier is set to true
	  */
	def identifier = apply(isIdentifier = Some(true))
	
	/**
	  * A data label model where isIdentifier is set to false
	  */
	def nonIdentifier = apply(isIdentifier = Some(false))
	
	/**
	  * @return A model where isEmail is set to true
	  */
	def email = apply(isEmail = Some(true))
	
	
	// IMPLEMENTED	---------------------
	
	
	override def apply(model: Model[Property]) = table.requirementDeclaration.validate(model).toTry.flatMap { model =>
		
		val dataType = model("dataType").int.flatMap(DataType.forInt)
		if (dataType.isEmpty)
			Failure(new NoSuchElementException(s"Couldn't reconize data type ${model("dataType").getInt}"))
		else
		{
			Success(ch.model.DataLabel(model("id").getInt,
				dataType.getOrElse(StringType), model("isIdentifier").getBoolean, model("isEmail").getBoolean))
		}
	}
}
