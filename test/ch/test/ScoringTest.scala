package ch.test

import java.time.Instant

import ch.model.DataType.BooleanType
import ch.model.scoring.{Algorithm, AlgorithmModifier, RiskFunction}
import ch.model.{CompanyDataLabelGroup, DataLabel, DataSet}
import utopia.flow.datastructure.immutable.Value
import utopia.flow.generic.DataType
import utopia.flow.generic.ValueConversions._

/**
 * Tests some scoring related features
 * @author Mikko Hilpinen
 * @since 28.8.2019, v1.1+
 */
object ScoringTest extends App
{
	DataType.setup()
	
	val emptyData = DataSet(Set())
	
	val perfectData = DataSet(Set(
		(DataLabel(6, BooleanType), true),
		(DataLabel(7, BooleanType), true),
		(DataLabel(8, BooleanType), true),
		(DataLabel(9, BooleanType), true),
		(DataLabel(10, BooleanType), true),
		(DataLabel(11, BooleanType), true),
		(DataLabel(12, BooleanType), true),
		(DataLabel(13, BooleanType), true),
		(DataLabel(14, BooleanType), true),
		(DataLabel(15, BooleanType), true),
		(DataLabel(16, BooleanType), true),
		(DataLabel(17, BooleanType), true),
		(DataLabel(18, BooleanType), true),
		(DataLabel(19, BooleanType), true),
		(DataLabel(20, BooleanType), true),
		(DataLabel(21, BooleanType), true),
		(DataLabel(22, BooleanType), true),
		(DataLabel(23, BooleanType), true),
		(DataLabel(24, BooleanType), true),
		(DataLabel(25, BooleanType), true),
		(DataLabel(26, BooleanType), true),
		(DataLabel(27, BooleanType), true),
		(DataLabel(28, BooleanType), true),
		(DataLabel(29, BooleanType), true),
		(DataLabel(30, BooleanType), true),
		(DataLabel(31, BooleanType), true),
		(DataLabel(32, BooleanType), true),
		(DataLabel(33, BooleanType), true),
		(DataLabel(34, BooleanType), true),
		(DataLabel(35, BooleanType), true),
		(DataLabel(36, BooleanType), true),
		(DataLabel(37, BooleanType), true)
	))
	
	val worstData = DataSet(Set(
		(DataLabel(6, BooleanType), false),
		(DataLabel(7, BooleanType), false),
		(DataLabel(8, BooleanType), false),
		(DataLabel(9, BooleanType), false),
		(DataLabel(10, BooleanType), false),
		(DataLabel(11, BooleanType), false),
		(DataLabel(12, BooleanType), false),
		(DataLabel(13, BooleanType), false),
		(DataLabel(14, BooleanType), false),
		(DataLabel(15, BooleanType), false),
		(DataLabel(16, BooleanType), false),
		(DataLabel(17, BooleanType), false),
		(DataLabel(18, BooleanType), false),
		(DataLabel(19, BooleanType), false),
		(DataLabel(20, BooleanType), false),
		(DataLabel(21, BooleanType), false),
		(DataLabel(22, BooleanType), false),
		(DataLabel(23, BooleanType), false),
		(DataLabel(24, BooleanType), false),
		(DataLabel(25, BooleanType), false),
		(DataLabel(26, BooleanType), false),
		(DataLabel(27, BooleanType), false),
		(DataLabel(28, BooleanType), false),
		(DataLabel(29, BooleanType), false),
		(DataLabel(30, BooleanType), false),
		(DataLabel(31, BooleanType), false),
		(DataLabel(32, BooleanType), false),
		(DataLabel(33, BooleanType), false),
		(DataLabel(34, BooleanType), false),
		(DataLabel(35, BooleanType), false),
		(DataLabel(36, BooleanType), false),
		(DataLabel(37, BooleanType), false)
	))
	
	// Tests basic algorithm modifiers first
	val basicMod = AlgorithmModifier(3, 1, Left(29), RiskFunction.FirstValue, 8, 0.2, 2)
	
	val perfectBasicResult = basicMod(perfectData)
	assert(perfectBasicResult._1 == 1.0)
	assert(perfectBasicResult._2 == 8)
	
	val worstBasicResult = basicMod(worstData)
	assert(worstBasicResult._1 == 0.0)
	assert(worstBasicResult._2 == 8)
	
	val missingResult = basicMod(emptyData)
	assert(missingResult._1 == 0.2)
	assert(missingResult._2 == 2)
	
	// Next tests average function
	val averageMod = AlgorithmModifier(1, 1, Right(CompanyDataLabelGroup(1, Vector(6, 7, 8, 9, 10, 11))),
		RiskFunction.Average, 8, 0.25, 2)
	
	val perfectAvgResult = averageMod(perfectData)
	assert(perfectAvgResult._1 == 1.0)
	assert(perfectAvgResult._2 == 8)
	
	val worstAvgResult = averageMod(worstData)
	assert(worstAvgResult._1 == 0.0)
	assert(worstAvgResult._2 == 8)
	
	val missingAvgResult = averageMod(emptyData)
	assert(missingAvgResult._1 == 0.25)
	assert(missingAvgResult._2 == 2)
	
	val partial1 = DataSet(Set(
		(DataLabel(6, BooleanType), true),
		(DataLabel(7, BooleanType), true),
		(DataLabel(8, BooleanType), true),
		(DataLabel(9, BooleanType), false),
		(DataLabel(10, BooleanType), false),
		(DataLabel(11, BooleanType), false),
		(DataLabel(31, BooleanType), true),
		(DataLabel(32, BooleanType), false)
	))
	
	val partAvgResult1 = averageMod(partial1)
	assert(partAvgResult1._1 == 0.5)
	assert(partAvgResult1._2 == 8)
	
	val partial2 = DataSet(Set(
		(DataLabel(6, BooleanType), true),
		(DataLabel(7, BooleanType), false),
		(DataLabel(31, BooleanType), true)
	))
	
	val partAvgResult2 = averageMod(partial2)
	assert(partAvgResult2._1 == 0.5)
	assert(partAvgResult2._2 == 8)
	
	// Next tests sequence function
	val listMod = AlgorithmModifier(2, 1, Right(CompanyDataLabelGroup(1, Vector(31, 32))),
		RiskFunction.Sequence, 13, 0.1, 3)
	
	assert(listMod(perfectData) == (1.0, 13))
	assert(listMod(worstData) == (0.0, 13))
	assert(listMod(emptyData) == (0.1, 3))
	assert(listMod(partial1) == (0.5, 13))
	assert(listMod(partial2) == (1.0, 13))
	
	val partial3 = DataSet(Set(
		(DataLabel(31, BooleanType), false),
		(DataLabel(32, BooleanType), true)
	))
	
	val partial4 = DataSet(Set(
		(DataLabel(32, BooleanType), true)
	))
	
	assert(listMod(partial3) == (0.0, 13))
	assert(listMod(partial4) == (1.0, 13))
	
	val algorithm = Algorithm(1, Instant.now, Set(basicMod, averageMod, listMod))
	assert(algorithm(perfectData) == 1.0)
	assert(algorithm(worstData) == 0.0)
	
	println(algorithm(emptyData))
	println(algorithm(partial1))
	println(algorithm(partial2))
	
	println("Success!")
}
