import { useEffect, useState } from "react";
import BookModel from "../../models/BookModel";
import { Pagination } from "../Utils/Pagination";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { SearchBook } from "./components/SearchBook";
import SearchIcon from "@mui/icons-material/Search";
import CategoryList from "../ManageLibraryPage/components/CategoryList";

export const SearchBooksPage = () => {
  // Categories
  const categories = [
    { label: "모든 분야", value: "모든 분야" },
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

  const [books, setBooks] = useState<BookModel[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [booksPerPage] = useState(5);
  const [totalAmountOfBooks, setTotalAmountOfBooks] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [searchUrl, setSearchUrl] = useState("");
  const [categorySelection, setCategorySelection] = useState("모든 분야");
  const [bookDelete, setBookDelete] = useState(false);

  useEffect(() => {
    const fetchBooks = async () => {
      const baseUrl: string = "http://localhost:8080/api/books";

      let url: string = "";

      if (searchUrl === "") {
        url = `${baseUrl}?page=${currentPage - 1}&size=${booksPerPage}`;
      } else {
        url = baseUrl + searchUrl;
      }

      const response = await fetch(url);

      if (!response.ok) {
        throw new Error("Something went wrong!");
      }

      const responseJson = await response.json();

      const responseData = responseJson._embedded.books;

      setTotalAmountOfBooks(responseJson.page.totalElements);
      setTotalPages(responseJson.page.totalPages);

      const loadedBooks: BookModel[] = [];

      for (const key in responseData) {
        loadedBooks.push({
          id: responseData[key].id,
          title: responseData[key].title,
          author: responseData[key].author,
          description: responseData[key].description,
          copies: responseData[key].copies,
          copiesAvailable: responseData[key].copiesAvailable,
          category: responseData[key].category,
          img: responseData[key].img,
        });
      }
      setBooks(loadedBooks);
      setIsLoading(false);
    };
    fetchBooks().catch((error: any) => {
      setIsLoading(false);
      setHttpError(error.message);
    });
    window.scrollTo(0, 0);
  }, [currentPage, searchUrl, bookDelete]);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  const searchHandleChange = () => {
    if (search === "") {
      setSearchUrl("");
    } else {
      setSearchUrl(
        `/search/findByTitleContaining?title=${search}&page=0&size=${booksPerPage}`
      );
    }
  };

  const categoryField = (value: string) => {
    if (
      value === "컴퓨터/IT" ||
      value === "자기계발" ||
      value === "만화" ||
      value === "경제/경영" ||
      value === "소설" ||
      value === "정치/사회" ||
      value === "인문" ||
      value === "종교" ||
      value === "시/에세이"
    ) {
      setCategorySelection(value);
      setSearchUrl(
        `/search/findByCategory?category=${value}&page=0&size=${booksPerPage}`
      );
    } else {
      setCategorySelection("모든 분야");
      setSearchUrl("");
    }
  };

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);
  const deleteBook = () => setBookDelete(!bookDelete);

  return (
    <div>
      <div className="container">
        <div>
          <div className="row mt-5">
            <div className="col-6">
              <div className="d-flex">
                <input
                  className="form-control me-2"
                  type="search"
                  placeholder="책 검색하기..."
                  aria-labelledby="Search"
                  onChange={(e) => setSearch(e.target.value)}
                />
                <button
                  className="btn btn-outline-success"
                  onClick={() => searchHandleChange()}
                >
                  <SearchIcon />
                </button>
              </div>
            </div>
            <div className="col-4">
              <div className="dropdown">
                <button
                  className="btn btn-secondary dropdown-toggle"
                  type="button"
                  id="dropdownMenuButton1"
                  data-bs-toggle="dropdown"
                  aria-expanded="false"
                >
                  {categorySelection}
                </button>
                <ul
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
            {totalAmountOfBooks > 0 ? (
              <>
                <div className="mt-3">
                  <h5>모든 책 수: {totalAmountOfBooks} 권</h5>
                </div>
                {books.map((book) => (
                  <SearchBook
                    book={book}
                    key={book.id}
                    deleteBook={deleteBook}
                  />
                ))}
              </>
            ) : (
              <div className="m-5">
                <h3>찾는 책이 없으신가요?</h3>
                <a
                  type="button"
                  className="btn btn-primary btn-md px-4 me-md-2 fw-bold text-white"
                  href="/messages"
                >
                  문의 하기
                </a>
              </div>
            )}
            {totalPages > 1 && (
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                paginate={paginate}
              />
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
