@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson6

import lesson6.impl.GraphBuilder
import java.util.ArrayDeque

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */

//трудоемкость: O(n)
//ресурсоемкость O(n)
//здесь n - кол-во вершин

fun Graph.findEulerLoop(): List<Graph.Edge> {
    val eulerLoop = mutableListOf<Graph.Edge>()
    if (!checkEuler()) return eulerLoop
    val stack = ArrayDeque<Graph.Vertex>()
    stack.push(vertices.first())
    val allEdges = edges
    while (stack.isNotEmpty()) {
        val curVertex = stack.peek()
        for (vertex in vertices) {
            val curEdge = getConnection(vertex, curVertex)
            if (curEdge != null && allEdges.contains(curEdge)) {  //нашли ребро, по которому ещё не прошли
                stack.push(vertex) //добавили новую вершину в стек
                allEdges.remove(curEdge)
                break
            }
        }
        if (curVertex == stack.peek()) {
            stack.pop()
            getConnection(curVertex, stack.peek())?.let { eulerLoop.add(it) }
        }
    }
    return eulerLoop
}

//Перед запуском алгоритма необходимо проверить граф на эйлеровость
//Для того, чтобы граф был эйлеровым необходимо чтобы:
//Все вершины имели четную степень.
//Все компоненты связности кроме, может быть одной, не содержали ребер.
fun Graph.checkEuler(): Boolean {
    if (vertices.isEmpty()) return false
    for (vertex in vertices) {
        if (getNeighbors(vertex).size % 2 != 0) return false //проверка первого пункта
    }
    //в visited будет находиться компонент связи, у которого есть ребра
    var visited = ArrayDeque<Graph.Vertex>()
    for (vertex in vertices) {
        if (getNeighbors(vertex).size != 0) {
            visited = dfs(vertex, ArrayDeque<Graph.Vertex>())
            break
        }
    }
    //если в visited нет вершины, она имеет соседние вершины, то это другая компонентная связь, содержащая вершины
    for (vertex in vertices)
        if (getNeighbors(vertex).size != 0 && !visited.contains(vertex)) return false //проверка второго пункта
    return true
}

fun Graph.dfs(vertex: Graph.Vertex, visited: ArrayDeque<Graph.Vertex>): ArrayDeque<Graph.Vertex> {
    for (curVertex in getNeighbors(vertex)) {
        if (!visited.contains(curVertex)) {
            visited.push(curVertex)
            dfs(curVertex, visited)
        }
    }
    return visited
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан связный граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */

//трудоемкость: O(n)
//ресурсоемкость O(n)
//здесь n - кол-во вершин

fun Graph.minimumSpanningTree(): Graph {
    val graph = GraphBuilder()
    if (!isConnected()) return graph.build()
    for (vertex in vertices) graph.addVertex(vertex.name)
    for ((vertex, vertexInfo) in shortestPath(vertices.first()))
        vertexInfo.prev?.let { graph.addConnection(it, vertex, 1) }
    return graph.build()
}

fun Graph.isConnected(): Boolean {
    if (vertices.isEmpty()) return false
    var visited = ArrayDeque<Graph.Vertex>()
    for (vertex in vertices) {
        if (getNeighbors(vertex).size != 0) {
            visited = dfs(vertex, ArrayDeque<Graph.Vertex>())
            break
        }
    }
    for (vertex in vertices)
        if (!visited.contains(vertex)) return false
    return true
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *
 * Если на входе граф с циклами, бросить IllegalArgumentException
 */
fun Graph.largestIndependentVertexSet(): Set<Graph.Vertex> {
    TODO()
}

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
fun Graph.longestSimplePath(): Path {
    TODO()
}

/**
 * Балда
 * Сложная
 *
 * Задача хоть и не использует граф напрямую, но решение базируется на тех же алгоритмах -
 * поэтому задача присутствует в этом разделе
 *
 * В файле с именем inputName задана матрица из букв в следующем формате
 * (отдельные буквы в ряду разделены пробелами):
 *
 * И Т Ы Н
 * К Р А Н
 * А К В А
 *
 * В аргументе words содержится множество слов для поиска, например,
 * ТРАВА, КРАН, АКВА, НАРТЫ, РАК.
 *
 * Попытаться найти каждое из слов в матрице букв, используя правила игры БАЛДА,
 * и вернуть множество найденных слов. В данном случае:
 * ТРАВА, КРАН, АКВА, НАРТЫ
 *
 * И т Ы Н     И т ы Н
 * К р а Н     К р а н
 * А К в а     А К В А
 *
 * Все слова и буквы -- русские или английские, прописные.
 * В файле буквы разделены пробелами, строки -- переносами строк.
 * Остальные символы ни в файле, ни в словах не допускаются.
 */
fun baldaSearcher(inputName: String, words: Set<String>): Set<String> {
    TODO()
}
