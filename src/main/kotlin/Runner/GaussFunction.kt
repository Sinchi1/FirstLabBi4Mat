package Runner

import java.util.*
import Runner.AdditionalFunctions.Programm
import org.apache.commons.math3.linear.ArrayRealVector
import org.apache.commons.math3.linear.LUDecomposition
import org.apache.commons.math3.linear.MatrixUtils
import org.apache.commons.math3.linear.RealVector
import java.io.File
import kotlin.math.absoluteValue
import kotlin.system.exitProcess

class GaussFunction {
    private val program = Programm()

    private fun gaussClassic(A: MutableList<MutableList<Double>>, B: MutableList<Double>): List<Float>? {
        val n = B.size

        if (A.size != n || A.any { it.size != n }) {
            println("Ошибка: Матрица коэффициентов должна быть квадратной и соответствовать размеру вектора правых частей!")
            return null
        }

        println("\nИсходная система:")
        program.customPrint(A, B, null)

        // Прямой ход метода Гаусса
        for (column in 0 until n) {
            val pivot = A[column][column]
            if (pivot == 0.0) {
                println("\nОшибка: Нулевой элемент на диагонали, метод Гаусса без выбора главного элемента не применим.")
                return null
            }

            println("\nНормализуем строку $column (делим на ${"%.20f".format(pivot)})")
            program.divideRow(A, B, column, pivot)
            program.customPrint(A, B, Pair(column, column))

            println("\nОбнуляем элементы ниже главного элемента")
            for (r in column + 1 until n) {
                if (A[r][column] != 0.0) {
                    program.combineRows(A, B, r, column, -A[r][column])
                    program.customPrint(A, B, Pair(r, column))
                }
            }
        }

        // Обратный ход
        val X = MutableList(n) { 0.0f }
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += A[i][j] * X[j]
            }
            X[i] = (B[i] - sum).toFloat()
        }

        // Вычисление невязок
        println("\nРешения: ")
        val residuals = MutableList(n) { 0.0 }
        X.forEachIndexed { index, x ->
            var sum = 0.0
            for (j in 0 until n) {
                sum += A[index][j] * X[j] // Ax
            }
            residuals[index] = sum - B[index] // Ax - B

            println("X${index + 1} = ${"%.5f".format(x)} \t Невязка R${index + 1} = ${(residuals[index].absoluteValue)}")
        }

        return X
    }


    private fun solveUsingLibrary(A: MutableList<MutableList<Double>>, B: MutableList<Double>): RealVector? {
        val arrayMatrix = A.map { it.toDoubleArray() }.toTypedArray()
        val coefficients = MatrixUtils.createRealMatrix(arrayMatrix)
        val constants = ArrayRealVector(B.toDoubleArray())

        return try {
            val solver = LUDecomposition(coefficients).solver
            if (!solver.isNonSingular) {
                println("Система не имеет единственного решения (матрица вырожденная).")
                return null
            }
            solver.solve(constants) // Возвращает вектор решений
        } catch (e: Exception) {
            println("Ошибка при решении: ${e.message}")
            null
        }
    }

    private fun determinantUsingLibrary(matrix: MutableList<MutableList<Double>>): Double {
        val arrayMatrix = matrix.map { it.toTypedArray().toDoubleArray() }.toTypedArray()
        return LUDecomposition(MatrixUtils.createRealMatrix(arrayMatrix)).determinant
    }

    private fun hasZeroRowsOrColumns(matrix: MutableList<MutableList<Double>>): Boolean {
        val n = matrix.size

        for (row in matrix) {
            if (row.all { it == 0.0 }) {
                println("Ошибка: Найдена нулевая строка в матрице!")
                return true
            }
        }

        // Проверяем нулевые столбцы
        for (col in 0 until n) {
            if (matrix.all { it[col] == 0.0 }) {
                println("Ошибка: Найден нулевой столбец в матрице!")
                return true
            }
        }

        return false
    }


    private fun gauss(A: MutableList<MutableList<Double>>, B: MutableList<Double>): List<Double>? {
        val n = B.size

        if (A.size != n || A.any { it.size != n }) {
            println("Ошибка: Матрица коэффициентов должна быть квадратной и соответствовать размеру вектора правых частей!")
            return null
        }

        var column = 0
        while (column < n) {
            println("\nИщем максимальный по модулю элемент в ${column + 1}-м столбце:")
            var currentRow: Int? = null

            for (r in column until n) {
                if (currentRow == null || kotlin.math.abs(A[r][column]) > kotlin.math.abs(A[currentRow][column])) {
                    currentRow = r
                }
            }

            if (currentRow == null || A[currentRow][column] == 0.0) {
                println("\n Решений нет (нулевой столбец в матрице).")
                return null
            }

            if (A[currentRow][column] == 0.0 ) {
                println("\n Решений нет (нулевой столбец в матрице).")
                return null
            }

            program.customPrint(A, B, Pair(currentRow, column))

            if (currentRow != column) {
                println("\nПереставляем строку с найденным элементом повыше:")
                program.swapRows(A, B, currentRow, column)
                program.customPrint(A, B, Pair(column, column))
            }

            println("\nНормализуем строку с найденным элементом:")
            val pivot = A[column][column]
            if (pivot == 0.0) {
                println("Ошибка: деление на ноль!")
                return null
            }

            program.divideRow(A, B, column, pivot)
            program.customPrint(A, B, Pair(column, column))

            println("\nОбрабатываем нижележащие строки:")
            for (r in column + 1 until n) {
                program.combineRows(A, B, r, column, -A[r][column])
            }

            program.customPrint(A, B, Pair(column, column))
            column++
        }

        println("\nМатрица приведена к треугольному виду, считаем решение... \n")
        val X = MutableList(n) { 0.0 }

        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += X[j] * A[i][j]
            }
            X[i] = B[i] - sum
        }

        println("Получили ответ:")
        val residuals = MutableList(n) { 0.0 }

        X.forEachIndexed { index, x ->
            var sum = 0.0
            for (j in 0 until n) {
                sum += A[index][j] * X[j] // Считаем Ax
            }
            residuals[index] = sum - B[index] // Вычисляем разницу Ax - B

            println("X${index + 1} = ${"%.5f".format(Locale.US, x)} \t Невязка R${index+1} = ${"%.30f".format(Locale.US, residuals[index].absoluteValue)}")
        }

        return X
    }

    private fun determinant(matrix: List<List<Double>>): Double {
        val n = matrix.size
        if (matrix.any { it.size != n }) {
            println("Ошибка: Матрица должна быть квадратной для вычисления определителя!")
            return Double.NaN
        }

        val A = matrix.map { it.toMutableList() }.toMutableList()
        var det = 1.0

        for (i in 0 until n) {
            var pivotRow = i
            for (r in i until n) {
                if (kotlin.math.abs(A[r][i]) > kotlin.math.abs(A[pivotRow][i])) {
                    pivotRow = r
                }
            }

            if (A[pivotRow][i] == 0.0) return 0.0

            if (pivotRow != i) {
                Collections.swap(A, i, pivotRow)
                det = -det
            }

            val pivot = A[i][i]
            if (pivot == 0.0) return 0.0
            det *= pivot

            for (r in i + 1 until n) {
                val factor = A[r][i] / pivot
                for (c in i until n) {
                    A[r][c] -= factor * A[i][c]
                }
            }
        }
        return det
    }

    fun main() {
        println("Введите 1 если ввод с клавиатуры \nВведите 0 если ввод с файла \nВвод:")
        val flag = readLine()?.toIntOrNull() ?: 1

        val myA: MutableList<MutableList<Double>>
        val myB: MutableList<Double>

        if (flag == 0) {
            println("Введите название файла:")
            val fileName = readlnOrNull()

            if (fileName.isNullOrEmpty() ) {
                println("Ошибка: Не указано имя файла или директория.")
                exitProcess(1)
            }

            val file = File(fileName)

            if (!file.exists()) {
                println("Ошибка: Файл не найден.")
                exitProcess(1)
            }

            try {
                val lines = file.readLines()
                val numRows = lines.firstOrNull()?.toIntOrNull()

                if (numRows == null || numRows <= 0) {
                    println("Ошибка: Некорректное количество строк в файле.")
                    exitProcess(1)
                }

                myA = lines.drop(1).take(numRows).map { line ->
                    line.split(" ").mapNotNull { it.toDoubleOrNull() }.toMutableList()
                }.toMutableList()

                myB = lines.drop(numRows + 1).firstOrNull()?.split(" ")?.mapNotNull { it.toDoubleOrNull() }?.toMutableList()
                    ?: mutableListOf()

            } catch (e: Exception) {
                println("Ошибка чтения файла: ${e.message}")
                exitProcess(1)
            }
        } else {
            println("Введите число строк матрицы коэффициентов:")
            val numRows = readLine()?.toIntOrNull() ?: 0
            if (numRows <= 0) {
                println("Ошибка: Некорректное число строк.")
                return
            }

            myA = mutableListOf()
            println("Введите матрицу коэффициентов построчно:")
            for (i in 0 until numRows) {
                val row = readLine()?.split(" ")?.mapNotNull { it.toDoubleOrNull() }?.toMutableList()
                if (row == null || row.size != numRows) {
                    println("Ошибка: Некорректный ввод строки матрицы.")
                    return
                }
                myA.add(row)
            }

            println("Введите вектор правых частей:")
            myB = readLine()?.split(" ")?.mapNotNull { it.toDoubleOrNull() }?.toMutableList() ?: mutableListOf()
            if (myB.size != numRows) {
                println("Ошибка: Размер вектора не соответствует количеству строк матрицы.")
                return
            }
        }

        println("Исходная система:")
        program.customPrint(myA, myB, null)

        println("\nПроверим СЛАУ на нулевые столбцы")
        if (!hasZeroRowsOrColumns(myA)){
            println("Система имеет решения!")
        }
        else{
            println("\nСистема не имеет решений ввиду нулевого столбца/строки. Выход из программы")
            exitProcess(1);
        }

        println("\nРешаем:")
        gaussClassic(myA, myB)
        println("\nОпределитель с помощью библиотеки ${determinantUsingLibrary(myA)}")
        println("\nОпределитель: ${determinant(myA)}")
        println("\nРешение с помощью библиотки: ${solveUsingLibrary(myA, myB)}")


    }
}

// библу
