package Runner

import java.util.*
import Runner.AdditionalFunctions.Programm

public class GaussFunction {

    private val program = Programm()

    fun gauss(
        A: MutableList<MutableList<Double>>,
        B: MutableList<Double>
    ): List<Double>? {
        var column = 0
        val n = B.size
        while (column < n) {
            println("Ищем максимальный по модулю элемент в ${column + 1}-м столбце:")
            var currentRow: Int? = null
            for (r in column until n) {
                if (currentRow == null || kotlin.math.abs(A[r][column]) > kotlin.math.abs(A[currentRow][column])) {
                    currentRow = r
                }
            }
            if (currentRow == null) {
                println("решений нет")
                return null
            }
            program.customPrint(A, B, Pair(currentRow, column))
            if (currentRow != column) {
                println("Переставляем строку с найденным элементом повыше:")
                program.swapRows(A, B, currentRow, column)
                program.customPrint(A, B, Pair(column, column))
            }
            println("Нормализуем строку с найденным элементом:")
            program.divideRow(A, B, column, A[column][column])
            program.customPrint(A, B, Pair(column, column))
            println("Обрабатываем нижележащие строки:")
            for (r in column + 1 until n) {
                program.combineRows(A, B, r, column, -A[r][column])
            }
            program.customPrint(A, B, Pair(column, column))
            column++
        }
        println("Матрица приведена к треугольному виду, считаем решение")
        val X = MutableList(n) { 0.0 }
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += X[j] * A[i][j]
            }
            X[i] = B[i] - sum
        }
        println("Получили ответ:")
        X.forEachIndexed { index, x ->
            println("X${index + 1} =\t${"%.2f".format(Locale.US, x)}")
        }
        return X
    }

    // Функция для вычисления определителя матрицы методом Гаусса
    fun determinant(matrix: List<List<Double>>): Double {
        val n = matrix.size
        // Делаем глубокую копию матрицы
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
                A[i] = A[pivotRow].also { A[pivotRow] = A[i] }
                det = -det
            }
            val pivot = A[i][i]
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
        // Ввод флага (1 - с клавиатуры, 0 - из файла)
        println("Введите 1 если ввод с клавиатуры \nВведите 0 если ввод с файла \nВвод:")
        val flag = readLine()?.toIntOrNull() ?: 1

        // Ввод матрицы коэффициентов
        println("введите число строк матрицы коэфициентов:")
        val numRows = readLine()?.toIntOrNull() ?: 0

        println("введите матрицу коэфициентов (по строках, пробелы между коэффициентами)")
        val myA = mutableListOf<MutableList<Double>>()
        for (i in 0 until numRows) {
            print("строка ${i + 1}: ")
            val row = readLine()?.split(" ")?.mapNotNull { it.toDoubleOrNull() }?.toMutableList() ?: mutableListOf()
            myA.add(row)
        }

        // Вычисляем определитель (аналог вызова opred.gauss)
        val det = determinant(myA)

        // Ввод вектора правых частей
        println("Введите матрицу правых частей, пробелы между числами:")
        val myB = readLine()
            ?.split(" ")
            ?.mapNotNull { it.toDoubleOrNull() }
            ?.toMutableList() ?: mutableListOf()

        if (numRows < myB.size) {
            println("Вектор результатов превышает размер строк матрицы коэф.")
            return
        }
        if (numRows > myB.size) {
            println("Вектор результатов не достигает размера матрицы коэф.")
            return
        }

        println("Исходная система:")
        program.customPrint(myA, myB, null)
        println("Решаем:")
        gauss(myA, myB)
        println("Определитель $det")
    }
}