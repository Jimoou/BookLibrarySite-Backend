import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { tossConfig } from "../../../lib/tossConfig";
import SuccessPurchaseRequest from "../../../models/SuccessPurchaseRequest";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";

export const Success = () => {
  const { authState } = useOktaAuth();
  const isLoading = true;

  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const paymentKey = String(searchParams.get("paymentKey"));
  const orderId = String(searchParams.get("orderId"));
  const amount = Number(searchParams.get("amount"));

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        const { approvedAt, status } = await confirmPayments();
        await successHandler(approvedAt, status);
        navigate("/success-complete");
      } catch (error: any) {
        console.log(error.message);
      }
    };

    if (authState && authState.isAuthenticated) {
      fetchData();
    }
  }, [authState]);

  const confirmPayments = async () => {
    const url = "https://api.tosspayments.com/v1/payments/confirm";
    const requestOptions = {
      method: "POST",
      headers: {
        Authorization: `Basic ${tossConfig.secretKey}`,
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        paymentKey: paymentKey,
        amount: amount,
        orderId: orderId,
      }),
    };
    const response = await fetch(url, requestOptions);

    if (!response.ok) {
      throw new Error("결제 승인 API 호출 실패");
    }
    const responseJson = await response.json();
    return { approvedAt: responseJson.approvedAt, status: responseJson.status };
  };

  const successHandler = async (purchaseDate: string, status: string) => {
    const successPurchase = new SuccessPurchaseRequest(
      paymentKey,
      orderId,
      purchaseDate,
      status
    );
    if (authState && authState.isAuthenticated) {
      const url = `${process.env.REACT_APP_API}/purchase/secure/update`;
      const requestOptions = {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${authState.accessToken?.accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(successPurchase),
      };
      const response = await fetch(url, requestOptions);
      if (!response.ok) {
        throw new Error("Something went wrong!");
      }
    }
  };

  return (
    <div className="container mt-5 mb-5">
      <h1>결제중입니다.</h1>
      <h2>{isLoading && <SpinnerLoading />}</h2>
    </div>
  );
};
