import React, { useEffect, useState } from "react";
import { fetchAlbums } from "./api/AlbumApi";
import { Album } from "./model/Album";
import AlbumGrid from "./components/AlbumGrid/AlbumGrid";
import { Box } from "@mui/material";
import Header from "./components/Header/Header";
import { useSnackbar } from "notistack";

const App = () => {
  const { enqueueSnackbar } = useSnackbar();
  const [albums, setAlbums] = useState<Album[]>([]);

  const fetchAllAlbums = async () => {
    try {
      const albums = await fetchAlbums();
      setAlbums(albums);
    } catch (e) {
      enqueueSnackbar("Error fetching albums", { variant: "error" });
    }
  };

  useEffect(() => {
    fetchAllAlbums();
  }, []);

  return (
    <Box
      sx={{
        width: "100%",
        height: "100vh",
        bgcolor: "background.default",
        color: "text.primary",
      }}
    >
      <Header />
      <Box
        sx={{
          width: "100%",
          height: "100%",
          display: "flex",
          flexDirection: "column",
          justifyContent: "space-between",
          alignItems: "start",
        }}
      >
        <Box />
        <AlbumGrid albums={albums} onFetchAlbums={() => fetchAllAlbums()} />
      </Box>
    </Box>
  );
};
export default App;
