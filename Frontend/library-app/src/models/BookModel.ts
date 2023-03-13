class BookModel {
  id: number;
  title: string;
  author?: string;
  description?: string;
  copies?: number;
  copiesAvailable?: number;
  category?: string;
  img?: string;
  publisher?: string;
  price: number;
  coin: number;
  publicationDate: string;

  constructor(
    id: number,
    title: string,
    author: string,
    description: string,
    copies: number,
    copiesAvailable: number,
    category: string,
    img: string,
    publihser: string,
    price: number,
    coin: number,
    publicationDate: string
  ) {
    this.id = id;
    this.title = title;
    this.author = author;
    this.description = description;
    this.copies = copies;
    this.copiesAvailable = copiesAvailable;
    this.category = category;
    this.img = img;
    this.publisher = publihser;
    this.price = price;
    this.coin = coin;
    this.publicationDate = publicationDate;
  }
}
export default BookModel;
