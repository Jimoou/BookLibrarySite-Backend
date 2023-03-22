package com.reactlibraryproject.springbootlibrary.Utils;

import com.reactlibraryproject.springbootlibrary.CustomExceptions.CartItemNotFoundException;
import com.reactlibraryproject.springbootlibrary.DAO.CartItemRepository;
import com.reactlibraryproject.springbootlibrary.Entity.CartItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartItemFinder {
    private CartItemRepository cartItemRepository;

    public CartItem itemFinder(String userEmail, Long id) {
        CartItem cartItem = cartItemRepository.findByUserEmailAndId(userEmail, id);
        if (cartItem == null) {
            throw new CartItemNotFoundException();
        }
        return cartItem;
    }
}
