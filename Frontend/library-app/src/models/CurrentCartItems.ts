import BookModel from "./BookModel";

class CurrentCartItems {
  book: BookModel;
  amount: number;
  cartItemId: number;

  constructor(book: BookModel, amount: number, cartItemId: number) {
    this.book = book;
    this.amount = amount;
    this.cartItemId = cartItemId;
  }
}
export default CurrentCartItems;
