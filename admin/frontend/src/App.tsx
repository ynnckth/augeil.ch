import React, { useEffect, useState } from "react";
import "./App.css";
import { fetchAlbums } from "./api/AlbumApi";
import { Album } from "./model/Album";
import AlbumGrid from "./components/AlbumGrid/AlbumGrid";

const App = () => {
  const [albums, setAlbums] = useState<Album[]>([]);

  useEffect(() => {
    fetchAlbums()
      .then((albums) => setAlbums(albums))
      .catch((e) => console.log(e));
  }, []);

  return (
    <div className="app">
      <AlbumGrid albums={albums} />
    </div>
  );
};
export default App;
