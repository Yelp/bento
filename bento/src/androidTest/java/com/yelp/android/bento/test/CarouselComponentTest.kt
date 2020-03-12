package com.yelp.android.bento.test

import com.yelp.android.bento.components.CarouselComponent
import com.yelp.android.bento.components.SimpleComponent
import org.junit.Assert
import org.junit.Test

class CarouselComponentTest {

    @Test
    fun addingAndRemovingComponents_updatesComponentGroup() {
        val carousel = CarouselComponent()
        carousel.addAll((1..20).map { SimpleComponent<Unit>(TestComponentViewHolder::class.java) })
        Assert.assertEquals(20, carousel.getItem(0).group.count)
        val testComponent = SimpleComponent<Unit>(TestComponentViewHolder::class.java)
        carousel.addComponent(testComponent)
        Assert.assertEquals(21, carousel.getItem(0).group.count)
        carousel.remove(testComponent)
        Assert.assertEquals(20, carousel.getItem(0).group.count)
        carousel.remove(0)
        Assert.assertEquals(19, carousel.getItem(0).group.count)
        carousel.clear()
        Assert.assertEquals(0, carousel.getItem(0).group.count)
    }
}
