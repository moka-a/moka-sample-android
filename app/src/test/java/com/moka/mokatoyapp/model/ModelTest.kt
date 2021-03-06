package com.moka.mokatoyapp.model

import com.moka.mokatoyapp.model.domain.BaseDomain
import com.moka.mokatoyapp.model.domain.Observer
import io.realm.annotations.Ignore
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import rx.observers.TestSubscriber
import java.util.concurrent.TimeUnit

open class ModelTest {

    @Test
    fun insertTest() {
        val subscriber = TestSubscriber.create<Observer.Data<MockModel>>()

        MockModel.ob.setOnChangeObservable().subscribe(subscriber)

        MockModel.insert({ mockModel ->
            mockModel.value = "test_value"
        })

        // assertion
        subscriber.awaitValueCount(1, 500, TimeUnit.MILLISECONDS)
        subscriber.assertValueCount(1)
        val result = subscriber.onNextEvents[0]
        assertThat(result).isNotNull()
        assertThat(result.isCreated).isTrue()
        assertThat(result.isUpdated).isFalse()
        assertThat(result.isDeleted).isFalse()
        assertThat(result.data.value).isEqualTo("test_value")
    }

    @Test
    fun updateTest() {
        val subscriber = TestSubscriber.create<Observer.Data<MockModel>>()

        MockModel.ob.setOnChangeObservable().subscribe(subscriber)

        val mockModel = MockModel()
        mockModel.value = "test_value"

        MockModel.update(mockModel, { mockModel ->
            mockModel.value = "test_update"
        })

        // assertion
        subscriber.awaitValueCount(1, 500, TimeUnit.MILLISECONDS)
        subscriber.assertValueCount(1)
        val result = subscriber.onNextEvents[0]
        assertThat(result).isNotNull()
        assertThat(result.isCreated).isFalse()
        assertThat(result.isUpdated).isTrue()
        assertThat(result.isDeleted).isFalse()
        assertThat(result.data.value).isEqualTo("test_update")
    }

    /**
     * temp mock domain
     */

    class MockModel : BaseDomain {

        var value: String? = null

        companion object {

            @Ignore
            var ob: Observer<MockModel> = Observer()

            fun insert(insert: (mockModel: MockModel) -> Unit): MockModel {
                val mockModel = MockModel()
                insert(mockModel)

                ob.onInsert(mockModel)
                return mockModel
            }

            fun update(mockModel: MockModel, update: (mockModel: MockModel) -> Unit) {
                update(mockModel)
                ob.onUpdate(mockModel)
            }

        }

    }
}