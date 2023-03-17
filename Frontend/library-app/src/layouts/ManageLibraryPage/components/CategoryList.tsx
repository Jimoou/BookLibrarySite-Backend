import React from "react";
import { MenuItem, Menu, Button } from "@mui/material";
import { ArrowDropDown } from "@mui/icons-material";
interface CategoryListProps {
  categories: { label: string; value: string }[];
  onSelect: (value: string) => void;
}

const CategoryList: React.FC<CategoryListProps> = ({
  categories,
  onSelect,
}) => {
  const [anchorEl, setAnchorEl] = React.useState<null | HTMLElement>(null);

  const handleClick = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleClose = () => {
    setAnchorEl(null);
  };

  const handleMenuItemClick = (value: string) => {
    onSelect(value);
    handleClose();
  };

  return (
    <>
      <Button variant="contained" color="secondary" onClick={handleClick}>
        카테고리 선택
        <ArrowDropDown />
      </Button>
      <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={handleClose}>
        {categories.map((category) => (
          <MenuItem
            key={category.value}
            onClick={() => handleMenuItemClick(category.value)}
          >
            {category.label}
          </MenuItem>
        ))}
      </Menu>
    </>
  );
};

export default CategoryList;
