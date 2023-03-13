import { useOktaAuth } from "@okta/okta-react";
import { useState } from "react";
import AddBookRequest from "../../../models/AddBookRequest";
import CategoryList from "./CategoryList";
export const AddNewBook = () => {
  const { authState } = useOktaAuth();

  // Categories
  const categories = [
    { label: "컴퓨터/IT", value: "컴퓨터/IT" },
    { label: "자기계발", value: "자기계발" },
    { label: "만화", value: "만화" },
    { label: "경제/경영", value: "경제/경영" },
    { label: "소설", value: "소설" },
    { label: "정치/사회", value: "정치/사회" },
    { label: "인문", value: "인문" },
    { label: "종교", value: "종교" },
    { label: "시/에세이", value: "시/에세이" },
  ];

  // New Book
  const [title, setTitle] = useState("");
  const [author, setAuthor] = useState("");
  const [description, setDescription] = useState("");
  const [copies, setCopies] = useState(0);
  const [category, setCategory] = useState("분야");
  const [selectedImage, setSelectedImage] = useState<any>(null);
  const [publisher, setPublisher] = useState("");
  const [price, setPrice] = useState(0);
  const [coin, setCoin] = useState(0);
  const [publicationDate, setPublicationDate] = useState("");

  // Displays
  const [displayWarning, setDisplayWarning] = useState(false);
  const [displaySuccess, setDisplaySuccess] = useState(false);

  function categoryField(value: string) {
    setCategory(value);
  }

  async function base64ConversionForImages(e: any) {
    if (e.target.files[0]) {
      getBase64(e.target.files[0]);
    }
  }
  function getBase64(file: any) {
    let reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = function () {
      setSelectedImage(reader.result);
    };
    reader.onerror = function (error) {
      console.log("Error", error);
    };
  }

  async function submitNewBook() {
    const url = `${process.env.REACT_APP_API}/admin/secure/add/book`;
    if (
      authState?.isAuthenticated &&
      title !== "" &&
      author !== "" &&
      category !== "Category" &&
      description !== "" &&
      copies >= 0 &&
      publisher !== "" &&
      price >= 0 &&
      coin >= 0 &&
      publicationDate !== ""
    ) {
      const book: AddBookRequest = new AddBookRequest(
        title,
        author,
        description,
        copies,
        category,
        publisher,
        price,
        coin,
        publicationDate
      );
      book.img = selectedImage;
      const requestOptions = {
        method: "POST",
        headers: {
          Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(book),
      };

      const submitNewBookResponse = await fetch(url, requestOptions);
      if (!submitNewBookResponse.ok) {
        throw new Error("Something went wrong!");
      }
      setTitle("");
      setAuthor("");
      setDescription("");
      setCopies(0);
      setCategory("분야");
      setSelectedImage(null);
      setPublisher("");
      setPrice(0);
      setCoin(0);
      setPublicationDate("");
      setDisplayWarning(false);
      setDisplaySuccess(true);
    } else {
      setDisplayWarning(true);
      setDisplaySuccess(false);
    }
  }

  return (
    <div className="container mt-5 mb-5">
      {displaySuccess && (
        <div className="alert alert-success" role="alert">
          책이 추가되었습니다.
        </div>
      )}
      {displayWarning && (
        <div className="alert alert-danger" role="alert">
          내용을 입력하세요.
        </div>
      )}
      <div className="card">
        <div className="card-header">책 추가하기</div>
        <div className="card-body">
          <form method="POST">
            <div className="row">
              <div className="col-md-6 mb-3">
                <label className="form-label">제목</label>
                <input
                  type="text"
                  className="form-control"
                  name="title"
                  required
                  onChange={(e) => setTitle(e.target.value)}
                  value={title}
                />
              </div>
              <div className="col-md-3 mb-3">
                <label className="form-label">저자</label>
                <input
                  type="text"
                  className="form-control"
                  name="author"
                  required
                  onChange={(e) => setAuthor(e.target.value)}
                  value={author}
                />
              </div>
              <div className="col-md-3 mb-3">
                <label className="form-label">출판사</label>
                <input
                  type="text"
                  className="form-control"
                  name="publisher"
                  required
                  onChange={(e) => setPublisher(e.target.value)}
                  value={publisher}
                />
              </div>
              <div className="col-md-3 mb-3">
                <label className="form-label">출판일</label>
                <input
                  type="date"
                  className="form-control"
                  name="publisher"
                  required
                  onChange={(e) => setPublicationDate(e.target.value)}
                  value={publicationDate}
                />
              </div>
              <div className="col-md-3 mb-3">
                <label className="form-label">분야</label>
                <button
                  className="form-control btn btn-secondary dropdown-toggle"
                  type="button"
                  id="dropdownMenuButton1"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  {category}
                </button>
                <ul
                  id="addNewBookId"
                  className="dropdown-menu"
                  aria-labelledby="dropdownMenuButton1"
                >
                  <CategoryList
                    categories={categories}
                    onSelect={categoryField}
                  />
                </ul>
              </div>
            </div>
            <div className="col-md-12 mb-3">
              <label className="form-label">설명</label>
              <textarea
                className="form-control"
                id="exampleFormControlTextarea1"
                rows={3}
                onChange={(e) => setDescription(e.target.value)}
                value={description}
              ></textarea>
            </div>
            <div className="col-md-3 mb-3">
              <label className="form-label">가격</label>
              <input
                type="number"
                className="form-control"
                name="price"
                required
                onChange={(e) => setPrice(Number(e.target.value))}
                value={price}
              />
            </div>
            <div className="col-md-3 mb-3">
              <label className="form-label">수량</label>
              <input
                type="number"
                className="form-control"
                name="Copies"
                required
                onChange={(e) => setCopies(Number(e.target.value))}
                value={copies}
              />
            </div>
            <input type="file" onChange={(e) => base64ConversionForImages(e)} />
            <div>
              <button
                type="button"
                className="btn btn-primary mt-3"
                onClick={submitNewBook}
              >
                추가
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};
