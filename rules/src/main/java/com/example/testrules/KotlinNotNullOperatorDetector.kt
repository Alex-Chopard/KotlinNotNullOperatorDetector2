package com.example.testrules

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*

@Suppress("UnstableApiUsage")
class KotlinNotNullOperatorDetector: Detector(), SourceCodeScanner {
    companion object {
        private val IMPLEMENTATION = Implementation(
            KotlinNotNullOperatorDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        private const val description = "The Not-Null operator !! is not safe to use!"

        val ISSUE: Issue = Issue
            .create(
                id = "KotlinNotNullOperatorDetector",
                briefDescription = description,
                explanation = """
                To avoid NullPointerExceptions don't use the !! operator.
            """.trimIndent(),
                category = Category.SECURITY,
                priority = 10,
                severity = Severity.ERROR,
                androidSpecific = true,
                implementation = IMPLEMENTATION
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>>? {
        return listOf(UPostfixExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler? = object: UElementHandler() {
        override fun visitPostfixExpression(node: UPostfixExpression) {
            if (node.operator.text == "!!") {
                reportUsage(context, node)
            }
        }
    }

    private fun reportUsage(context: JavaContext, node: UPostfixExpression) {
        context.report(
            issue = ISSUE,
            scope = node,
            location = context.getLocation(node),
            message = description
        )
    }
}