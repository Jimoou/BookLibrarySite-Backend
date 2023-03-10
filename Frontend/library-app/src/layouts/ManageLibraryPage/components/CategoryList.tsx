import React from "react";

interface CategoryListProps {
  categories: { label: string; value: string }[];
  onSelect: (value: string) => void;
}

const CategoryList: React.FC<CategoryListProps> = ({
  categories,
  onSelect,
}) => {
  return (
    <>
      {categories.map((category) => (
        <li key={category.value}>
          <span
            onClick={() => onSelect(category.value)}
            className="dropdown-item"
          >
            {category.label}
          </span>
        </li>
      ))}
    </>
  );
};

export default CategoryList;
