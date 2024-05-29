package com.example.libapp.basket

import com.example.libapp.database.Book
import com.example.libapp.database.Basket
import com.example.libapp.database.BasketDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BasketRepository(private val basketDao: BasketDao) {

    suspend fun manageBookInBasket(userId: Long, dish: Book, action: Int) {
        withContext(Dispatchers.IO) {
            // Получаем текущую корзину пользователя
            var basket = basketDao.getBasketByUserId(userId)

            // Если корзина не существует, создаем новую
            if (basket == null) {
                if (action == 1) { // Добавление товара
                    basket = Basket(composition = mutableListOf(dish), userId = userId)
                    basketDao.insertBasket(basket)
                }
            } else {
                val existingDish = basket.composition.find { it.id == dish.id }

                if (existingDish == null) {
                    if (action == 1) { // Добавление товара
                        // Добавляем блюдо в текущую корзину
                        basket.composition.add(dish)
                        basketDao.updateBasket(basket)
                    }
                } else {
                    if (action == 0) { // Уменьшение количества товара
                        // Уменьшаем количество товара в корзине
                        existingDish.quantity = existingDish.quantity - 1
                        if (existingDish.quantity <= 0) {
                            // Удаляем блюдо из корзины, если количество стало <= 0
                            basket.composition.remove(existingDish)
                        }
                        basketDao.updateBasket(basket)
                    } else if (action == 1) { // Увеличение количества товара
                        // Увеличиваем количество товара в корзине
                        existingDish.quantity = existingDish.quantity + 1
                        basketDao.updateBasket(basket)
                    }
                }
            }
        }
    }
}