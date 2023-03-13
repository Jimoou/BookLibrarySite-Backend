import BookModel from "./BookModel";

class BooksInCart {
  book: BookModel;
  amount: number;

  constructor(book: BookModel, amount: number) {
    this.book = book;
    this.amount = amount;
  }
}
export default BooksInCart;
