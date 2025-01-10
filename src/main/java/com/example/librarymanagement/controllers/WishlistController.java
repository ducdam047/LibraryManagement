package com.example.librarymanagement.controllers;

import com.example.librarymanagement.dtos.models.WishlistModel;
import com.example.librarymanagement.dtos.requests.wishlist.AddToWishlistRequest;
import com.example.librarymanagement.dtos.responses.ApiResponse;
import com.example.librarymanagement.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @GetMapping()
    public ResponseEntity<List<WishlistModel>> getAllWishlist() {
        List<WishlistModel> wishlistModels = wishlistService.getAllWishlist();
        return ResponseEntity.ok(wishlistModels);
    }

    @PostMapping("/addToWishlist")
    public ApiResponse<WishlistModel> addWishlist(@RequestBody AddToWishlistRequest request) {
        return ApiResponse.<WishlistModel>builder()
                .code(201)
                .message("The book was added to wish list")
                .data(wishlistService.addWishlist(request))
                .build();
    }
}
