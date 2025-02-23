package org.truskovski.Runner.AdditionalFunctions

import java.util.*

class Programm {

    fun print(
        A: List<List<Double>>,
        B: List<Double>,
        selected: Pair<Int, Int>?
    ){
        for (row in B.indices){
            print("(")
            for (col in A[row].indices){
                val marker = if (selected != null && selected.first == row && selected.second == col)  "*" else ""
                print("\t${"%.2f".format(Locale.US, A[row][col])}$marker")
            }
        }

    }

    fun swapRows(
        A: MutableList<MutableList<Double>>,
        B: MutableList<Double>,
        row1: Int,
        row2: Int
    ){
      A[row1] = A[row2].also { A[row2] = A[row1] }
      B[row1] = B[row2].also { B[row2] = B[row1]}
    }

    fun divideRow(
        a: MutableList<MutableList<Double>>,
        b: MutableList<Double>,
        row: Int,
        divider: Double
    ){
        for (i in a[row].indices){
            a[row][i] /= divider
        }
        b[row] /= divider
    }

    fun combineRows(
        a: MutableList<MutableList<Double>>,
        b: MutableList<Double>,
        row: Int,
        sourceRow: Int,
        weight: Double
    ){
        for (i in a[row].indices){
            a[row][i] += a[sourceRow][i] * weight
        }
        b[row] += b[sourceRow] * weight
    }

}

