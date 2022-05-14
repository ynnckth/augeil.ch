import { Album } from "../model/Album";

export const fetchAlbums = async (): Promise<Album[]> => {
  const response = await fetch("/albums");
  return await response.json();
};
