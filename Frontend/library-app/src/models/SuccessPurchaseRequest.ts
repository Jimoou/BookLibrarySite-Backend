class SuccessPurchaseRequest {
  paymentKey: string;
  orderId: string;
  purchaseDate: string;
  status: string;
  constructor(
    paymentKey: string,
    orderId: string,
    purchaseDate: string,
    status: string
  ) {
    this.paymentKey = paymentKey;
    this.orderId = orderId;
    this.purchaseDate = purchaseDate;
    this.status = status;
  }
}
export default SuccessPurchaseRequest;
