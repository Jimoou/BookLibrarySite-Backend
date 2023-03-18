class PurchaseHistory {
  paymentKey: string;
  orderName: string;
  totalPrice: number;
  purchaseDate: string;
  constructor(
    paymentKey: string,
    oderName: string,
    totalPrice: number,
    purchaseDate: string
  ) {
    this.paymentKey = paymentKey;
    this.orderName = oderName;
    this.totalPrice = totalPrice;
    this.purchaseDate = purchaseDate;
  }
}
export default PurchaseHistory;
