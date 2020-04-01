package com.ordy.backend.database

import com.ordy.backend.database.models.*
import com.ordy.backend.database.repositories.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.util.Assert
import java.util.*
import java.util.logging.Logger


@DataJpaTest(showSql = false)
class DatabaseTest {
    @Autowired
    lateinit var userRepository: UserRepository
    @Autowired
    lateinit var locationRepository: LocationRepository
    @Autowired
    lateinit var orderRepository: OrderRepository
    @Autowired
    lateinit var orderItemRepository: OrderItemRepository
    @Autowired
    lateinit var cuisineRepository: CuisineRepository
    @Autowired
    lateinit var itemRepository: ItemRepository
    @Autowired
    lateinit var groupRepository: GroupRepository
    @Autowired
    lateinit var groupMemberRepository: GroupMemberRepository
    @Autowired
    lateinit var groupInviteRepository: GroupInviteRepository

    private val user = User(username = "Bob", email = "Bob@mail.com", password = "yeetskeet")
    private val cuisine = Cuisine(name = "Cuisine")
    private val location = Location(name = "Frituur", latitude = 0.0, longitude = 0.0, address = "Straat 0", private = false, cuisine = cuisine)
    private val group = Group(name = "ZeusWPI", creator = user)

    private val log = Logger.getLogger("Test")

    @BeforeEach
    fun populate_db() {
        for (i in 1..20) {
            val tmpUser = User(username = "user$i", email = "user$i@mail.com", password = "yeetskeet")
            val tmpGroup = Group(name = "User $i 's group", creator = tmpUser)
            userRepository.save(tmpUser)
            groupRepository.save(tmpGroup)
            if (i % 2 == 0) {
                groupMemberRepository.save(GroupMember(user = tmpUser, group = tmpGroup))
            } else {
                groupInviteRepository.save(GroupInvite(user = tmpUser, group = tmpGroup))
            }

            val tmpCuisine = Cuisine(name = "Cuisine $i")
            for (j in 1..20) {
                val tmpItem = Item(name = "Item $j for cuisine $i")
                itemRepository.save(tmpItem)
            }

            val tmpLocation = Location(name = "Frituur $i", latitude = 0.0, longitude = 0.0, address = "Straat $i",
                    private = false, cuisine = cuisine)
            locationRepository.save(tmpLocation)
            cuisineRepository.save(tmpCuisine)
            val tmpOrder = Order(deadline = Date(), group = tmpGroup, courier = tmpUser, location = tmpLocation)
            val tmpOrderItem = OrderItem(order = tmpOrder, user = tmpUser, item = itemRepository.findAll().first())
            orderRepository.save(tmpOrder)
            orderItemRepository.save(tmpOrderItem)
        }
    }

    @Test
    fun `Should find user when multiple users in db`() {
        userRepository.save(user)
        val found = userRepository.findById(user.id)
        Assert.isTrue(!found.isPresent, "not found")
        Assert.isTrue(found.get() == user, "not the same")
    }

    @Test
    fun `Should find cuisine when multiple cuisines in db`() {
        cuisineRepository.save(cuisine)
        val found = cuisineRepository.findById(cuisine.id)
        Assert.isTrue(!found.isPresent, "not found")
        Assert.isTrue(found.get() == cuisine, "not the same")
    }

    @Test
    fun `Should find location when multiple locations in db`() {
        locationRepository.save(location)
        val found = locationRepository.findById(location.id)
        Assert.isTrue(!found.isPresent, "not found")
        Assert.isTrue(found.get() == location, "not the same")
    }


    @Test
    fun `cuisine should have 400 items`() {
        cuisineRepository.save(cuisine)
        cuisineRepository.findById(cuisine.id).ifPresentOrElse(
                { Assert.isTrue(it.items.size == 400, "Size was ${it.items.size} not 400") },
                { throw AssertionError("cuisine not found") })
    }

    @Test
    fun `cuisine, adding item twice should not increase items size`() {
        cuisineRepository.save(cuisine)
        val tmpItem = Item(name = "yes")
        cuisine.items.add(tmpItem)
        val itemSize = cuisine.items.size
        cuisine.items.add(tmpItem)
        Assert.isTrue(itemSize == cuisine.items.size, "size did not match")
    }
}
