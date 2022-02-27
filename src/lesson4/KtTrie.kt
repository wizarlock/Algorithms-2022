package lesson4

import java.lang.IllegalStateException
import java.util.*

/**
 * Префиксное дерево для строк
 */
class KtTrie : AbstractMutableSet<String>(), MutableSet<String> {

    private class Node {
        val children: SortedMap<Char, Node> = sortedMapOf()
    }

    private val root = Node()

    override var size: Int = 0
        private set

    override fun clear() {
        root.children.clear()
        size = 0
    }

    private fun String.withZero() = this + 0.toChar()

    private fun findNode(element: String): Node? {
        var current = root
        for (char in element) {
            current = current.children[char] ?: return null
        }
        return current
    }

    override fun contains(element: String): Boolean =
        findNode(element.withZero()) != null

    override fun add(element: String): Boolean {
        var current = root
        var modified = false
        for (char in element.withZero()) {
            val child = current.children[char]
            if (child != null) {
                current = child
            } else {
                modified = true
                val newChild = Node()
                current.children[char] = newChild
                current = newChild
            }
        }
        if (modified) {
            size++
        }
        return modified
    }

    override fun remove(element: String): Boolean {
        val current = findNode(element) ?: return false
        if (current.children.remove(0.toChar()) != null) {
            size--
            return true
        }
        return false
    }

    /**
     * Итератор для префиксного дерева
     *
     * Спецификация: [java.util.Iterator] (Ctrl+Click по Iterator)
     *
     * Сложная
     */

    //тот же BinarySearchTreeIterator, только заполнение stack другое (заполняем сразу, так как порядок не имеет значения)
    override fun iterator(): MutableIterator<String> = TrieIterator()

    inner class TrieIterator internal constructor() : MutableIterator<String> {
        private var stack = ArrayDeque<String>()

        init {
            initStack(root, "")
        }

        //пока не найдем '0' рекурсивно перебираем детей, далее вернемся назад и будем снова искать '0', перебирая других детей
        private fun initStack(node: Node, str: String) {
            for ((char, childNode) in node.children)
                if (char != 0.toChar()) initStack(childNode, str + char)
                else stack.push(str)
        }

        //трудоемкость: O(1)
        //ресурсоемкость O(1)

        override fun hasNext(): Boolean = stack.isNotEmpty()

        private var next: String = ""

        //трудоемкость: O(1)
        //ресурсоемкость O(1)

        override fun next(): String {
            if (!hasNext()) throw NoSuchElementException()
            next = stack.pop()
            return next
        }

        //трудоемкость: O(logn)
        //ресурсоемкость O(1)

        override fun remove() {
            if (next == "") throw IllegalStateException()
            remove(next)
            next = ""
        }
    }
}