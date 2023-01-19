import React from "react";
import { Footer } from "./layouts/NavbarAndFooter/Footer";
import { Navbar } from "./layouts/NavbarAndFooter/Navbar";
import { HomPage } from "./layouts/HomePage/HomePage";
import { SearchBooksPage } from "./layouts/SearchBooksPage/SearchBooksPage";
import { Navigate, Route, Routes } from "react-router-dom";
import { BookCheckoutPage } from "./layouts/BookCheckoutPage/BookCheckouPage";

function App() {
  return (
    <div className="d-flex flex-column min-vh-100">
      <Navbar />
      <div className='flex-grow-1'>
      <Routes>
        <Route path="/home" element={<HomPage />} />
        <Route path="/" element={<Navigate replace to="/home"/>} />
        <Route path="/search" element={<SearchBooksPage />} />
        <Route path="/checkout/:bookId" element={<BookCheckoutPage />} />
      </Routes>
      </div>
      <Footer />
    </div>
  );
}

export default App;
