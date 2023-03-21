class PaymentHistory {
  title: string;
  author: string;
  category: number;
  img: string;
  publisher: string;
  amount: number;
  price: number;
  paymentDate: string;
  orderId: string;
  constructor(
    title: string,
    author: string,
    category: number,
    img: string,
    publisher: string,
    amount: number,
    price: number,
    paymentDate: string,
    orderId: string
  ) {
    this.title = title;
    this.author = author;
    this.category = category;
    this.img = img;
    this.publisher = publisher;
    this.amount = amount;
    this.price = price;
    this.paymentDate = paymentDate;
    this.orderId = orderId;
  }
}
export default PaymentHistory;
