package com.user.user_service.Controllers;


import com.user.user_service.Constants.PageConstant;
import com.user.user_service.DTOs.PageInfo;
import com.user.user_service.DTOs.User.UserResponse;
import com.user.user_service.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminController {
    private final UserService userService;

    @GetMapping()
    public ResponseEntity<?> getAll(
            @RequestParam(required = false, defaultValue = PageConstant.PAGE_NUMBER)Integer pageNumber,
            @RequestParam(required = false,defaultValue = PageConstant.PAGE_SIZE)Integer pageSize,
            @RequestParam(required = false,defaultValue = PageConstant.SORT_BY) String sortBy,
            @RequestParam(required = false,defaultValue = PageConstant.SORT_DIR)String sortDir
    )
    {
        PageInfo<UserResponse> responsePageInfo = this.userService.getAll(
                pageNumber,
                pageSize,
                sortBy,
                sortDir
        );
        return ResponseEntity.ok(responsePageInfo);
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(
            @PathVariable Long id
    ){
        this.userService.deactivateUser(id);

        return ResponseEntity.ok(Map.of(
                "message","User deactivated"
        ));
    }


    @PatchMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(
            @PathVariable Long id
    ){
        this.userService.activateUser(id);

        return ResponseEntity.ok(Map.of(
                "message","User activated"
        ));
    }


}
