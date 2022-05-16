import React from "react";
import { Box, Typography } from "@mui/material";
import logo from "../../assets/logo-white-header.svg";

const Header = () => {
  return (
    <Box
      sx={{
        display: "flex",
        alignItems: "center",
        justifyContent: "space-between",
        paddingLeft: "10px",
        backgroundColor: "#272727",
      }}
    >
      <img src={logo} alt="logo" width="35px" style={{ margin: "10px" }} />
      <Typography
        variant="h6"
        noWrap
        component="div"
        sx={{ flexGrow: 1, fontWeight: 700, maxWidth: "200px" }}
      >
        AUGEIL ADMIN
      </Typography>
      <Box />
    </Box>
  );
};
export default Header;
