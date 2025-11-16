package com.skku_team2.skku_helper

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SharedPreferencesUnitTest {
    object TestPrefFiles {
        const val TEST = "com.skku_team2.skku_helper.TEST"
    }
    object TestPrefKeys {
        object Test {
            const val FILE1 = "com.skku_team2.skku_helper.test.FILE1"
            const val FILE2 = "com.skku_team2.skku_helper.test.FILE2"
        }
    }

    @Test
    fun testSharedPreferences() {
        Log.d("testSharedPreferences", "테스트 시작")

        val file1 = "안녕하세요. 반갑습니다!"
        val file2 = 1234

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val sharedPreferences = appContext.getSharedPreferences(TestPrefFiles.TEST, Context.MODE_PRIVATE)

        sharedPreferences.edit {
            Log.d("testSharedPreferences", "SharedPreferences 작성")
            putString(TestPrefKeys.Test.FILE1, file1)
            putInt(TestPrefKeys.Test.FILE2, file2)
        }

        val loadedFile1 = sharedPreferences.getString(TestPrefKeys.Test.FILE1, null)
        val loadedFile2 = sharedPreferences.getInt(TestPrefKeys.Test.FILE2, -1)
        run {
            Log.d("testSharedPreferences", "SharedPreferences 읽기")
            Log.d("testSharedPreferences", "FILE1: $loadedFile1")
            Log.d("testSharedPreferences", "FILE1: $loadedFile2")
        }

        assert(file1 == loadedFile1)
        assert(file2 == loadedFile2)

        Log.d("testSharedPreferences", "테스트 종료")
    }
}
