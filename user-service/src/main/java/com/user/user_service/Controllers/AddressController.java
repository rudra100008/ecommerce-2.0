package com.user.user_service.Controllers;

import com.user.user_service.Constants.PageConstant;
import com.user.user_service.DTOs.Address.AddressRequest;
import com.user.user_service.DTOs.Address.AddressResponse;
import com.user.user_service.DTOs.Address.UpdateAddressRequest;
import com.user.user_service.Services.AddressService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.LongStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users/addresses")
public class AddressController {
    private final AddressService addressService;



    // add the address of user
    @PostMapping
    public ResponseEntity<?> add(
            @Valid  @RequestBody AddressRequest addressRequest,
            @RequestHeader("X-User-Id") Long userId
    ){
        AddressResponse addressResponse = this.addressService.add(userId, addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(addressResponse);
    }


    // update the address of user
    @PutMapping()
    public  ResponseEntity<?> update(
            @Valid @RequestBody UpdateAddressRequest updateAddressRequest,
            @RequestHeader("X-User-Id") Long userId
    ){
        AddressResponse addressResponse = this.addressService.update(userId,updateAddressRequest);
        return ResponseEntity.status(HttpStatus.OK).body(addressResponse);
    }

    // get all the address of a user
    @GetMapping
    public ResponseEntity<?> getAll(
            @RequestHeader("X-User-Id") Long userId
    ){
        List<AddressResponse> addressResponseList = this.addressService.getAll(userId);
        return ResponseEntity.status(HttpStatus.OK).body(addressResponseList);
    }

    // delete a address of user
    @DeleteMapping("/{addressId}")
    public ResponseEntity<?> delete(
            @NotNull(message = "Address Id is required") @PathVariable Long addressId,
            @RequestHeader("X-User-Id") Long userId
    ){
        this.addressService.delete(userId,addressId);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "message","Deleted address successfully"
        ));
    }
}
