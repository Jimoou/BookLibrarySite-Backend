package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.RequestModels.AddBookRequest;
import com.reactlibraryproject.springbootlibrary.Service.AdminService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private AdminService adminService;

    @PostMapping("/secure/add/book")
    public void postBook(@RequestHeader(value="Authorization") String token,
                         @RequestBody AddBookRequest addBookRequest) throws Exception {
        String admin = ExtractJWT.payloadJWTExtraction(token, "\"userType\"");
        if (admin == null || !admin.equals("admin")) {
            throw new Exception("Administration page only");
        }
        adminService.postBook(addBookRequest);
    }
}