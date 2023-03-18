package com.reactlibraryproject.springbootlibrary.Controller;

import com.reactlibraryproject.springbootlibrary.ReponseModels.PurchaseHistoryResponse;
import com.reactlibraryproject.springbootlibrary.RequestModels.AddPurchaseRequest;
import com.reactlibraryproject.springbootlibrary.RequestModels.SuccessPurchaseRequest;
import com.reactlibraryproject.springbootlibrary.Service.PurchaseService;
import com.reactlibraryproject.springbootlibrary.Utils.ExtractJWT;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("https://localhost:3000")
@RestController
@AllArgsConstructor
@RequestMapping("/api/purchase")
@Tag(name = "결제", description = "결제 API")
public class PurchaseController {

    private PurchaseService purchaseService;

    @Operation(summary = "결제 내역")
    @GetMapping("/secure/histories")
    public List<PurchaseHistoryResponse> purchaseHistories(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        return purchaseService.purchaseHistories(userEmail);
    }
    @Operation(summary = "결제 승인 전 DB에 추가")
    @PostMapping("/secure")
    public void addPendingPurchases(@RequestHeader(value = "Authorization") String token,
                                @RequestBody List<AddPurchaseRequest> purchaseRequests) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        purchaseService.addPendingPurchases(userEmail, purchaseRequests);
    }

    @Operation(summary = "결제 승인 전 오류시 DB에서 삭제")
    @DeleteMapping("/secure/delete/fail")
    public void deleteFailPurchase(@RequestHeader(value = "Authorization") String token) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        purchaseService.deleteFailPurchase(userEmail);
    }

    @Operation(summary = "결제 승인후 결제 내역 update")
    @PutMapping("/secure/update")
    public void updateSuccessPurchase(@RequestHeader(value = "Authorization") String token,
                                @RequestBody SuccessPurchaseRequest purchaseRequests) throws Exception {
        String userEmail = ExtractJWT.payloadJWTExtraction(token, "\"sub\"");
        purchaseService.updateSuccessPurchase(userEmail, purchaseRequests);
    }
}
