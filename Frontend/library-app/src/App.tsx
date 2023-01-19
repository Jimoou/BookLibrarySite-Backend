import React from "react";
import { Footer } from "./layouts/NavbarAndFooter/Footer";
import { Navbar } from "./layouts/NavbarAndFooter/Navbar";
import { HomPage } from "./layouts/HomePage/HomePage";
import { SearchBooksPage } from "./layouts/SearchBooksPage/SearchBooksPage";
import { Navigate, Route, Routes } from "react-router-dom";

function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/home" element={<HomPage />} />
        <Route path="/" element={<Navigate replace to="/home"/>} />
        <Route path="/search" element={<SearchBooksPage />} />
      </Routes>
      <Footer />
    </>
  );
}

export default App;
