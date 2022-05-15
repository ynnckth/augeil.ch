import React from "react";
import {AppBar, Box, Typography} from "@mui/material";
import logo from "../../assets/logo-white-header.svg";

// TODO: fix positioning issue with page height
const Header = () => {
  return (
    <AppBar color="primary" position="fixed">
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          marginLeft: "10px",
        }}
      >
        <img src={logo} alt="logo" width="35px" style={{ margin: "10px" }} />
        <Typography
          variant="h6"
          noWrap
          component="div"
          sx={{ flexGrow: 1, fontWeight: 700 }}
        >
          AUGEIL ADMIN
        </Typography>
      </Box>
    </AppBar>
  );
};
export default Header;
