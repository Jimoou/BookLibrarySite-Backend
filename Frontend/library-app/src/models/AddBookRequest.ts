class AddBookRequest {
  title: string;
  author: string;
  description: string;
  copies: number;
  category: string;
  img?: string;
  publisher: string;
  price: number;
  coin: number;
  publicationDate: string;

  constructor(
    title: string,
    author: string,
    description: string,
    copies: number,
    category: string,
    publisher: string,
    price: number,
    coin: number,
    publicationDate: string
  ) {
    this.title = title;
    this.author = author;
    this.description = description;
    this.copies = copies;
    this.category = category;
    this.publisher = publisher;
    this.price = price;
    this.coin = coin;
    this.publicationDate = publicationDate;
  }
}
export default AddBookRequest;
