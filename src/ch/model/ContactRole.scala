package ch.model

/**
  * Represents a contact's role in a company
  * @author Mikko Hilpinen
  * @since 10.7.2019, v0.1+
  * @param id This role's unique identifier
  * @param isInsideCompany Whether this role is inside (true) or outside (false) targeted company
  */
@deprecated("Replaced with EntityLinkType", "2+")
case class ContactRole(id: Int, isInsideCompany: Boolean)
{
	override def toString = s"role $id (${if (isInsideCompany) "inside" else "outside"})"
}
