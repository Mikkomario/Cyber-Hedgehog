package ch.client.model

/**
 * Used for specifying margins for various contexts
 * @author Mikko Hilpinen
 * @since 7.11.2019, v3+
 * @param medium Standard medium margin
 */
case class Margins(medium: Int)
{
	// COMPUTED	--------------------
	
	/**
	 * @return Smaller margin (used for related components and in tight spaces)
	 */
	def small = medium / 2
	
	/**
	 * @return A very small margin
	 */
	def mini = medium / 4
	
	/**
	 * @return Larger margin (used when there's a lot of space between non-related components)
	 */
	def large = medium * 2
}
