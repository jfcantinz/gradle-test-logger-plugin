package com.adarshr.gradle.testlogger.theme

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.util.TimeUtils
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult

@SuppressWarnings("GrMethodMayBeStatic")
abstract class AbstractTheme implements Theme {

    protected final boolean showExceptions
    protected final long slowThreshold

    AbstractTheme(TestLoggerExtension extension) {
        this.showExceptions = extension.showExceptions
        this.slowThreshold = extension.slowThreshold
    }

    @Override
    String exceptionText(TestDescriptor descriptor, TestResult result) {
        exceptionText(descriptor, result, 2)
    }

    protected String escape(String text) {
        text
            .replace('\u001B', '')
            .replace('[', '\\[')
            .replace(']', '\\]')
    }

    protected String exceptionText(TestDescriptor descriptor, TestResult result, int indent) {
        def line = new StringBuilder()

        if (showExceptions) {
            def indentation = ' ' * indent

            line << '\n\n'

            line << result.exception.toString().trim().readLines().collect {
                "${indentation}${escape(it)}"
            }.join('\n')

            line << '\n'

            line << result.exception.stackTrace.find {
                it.className == descriptor.className
            }.collect {
                "${indentation}    at ${escape(it.toString())}"
            }.join('\n')

            line << '\n'
        }

        line
    }

    protected boolean tooSlow(TestResult result) {
        (result.endTime - result.startTime) >= slowThreshold
    }

    protected String duration(TestResult result) {
        TimeUtils.humanDuration(result.endTime - result.startTime)
    }
}
