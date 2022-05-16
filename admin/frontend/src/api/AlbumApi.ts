import { Album } from "../model/Album";

export const fetchAlbums = async (): Promise<Album[]> => {
  const response = await fetch("/albums");
  return await response.json();
};

export const uploadAlbum = async (
  artist: string,
  album: string,
  albumZipFile: File
): Promise<Album> => {
  const formData = new FormData();
  formData.append("artist", artist);
  formData.append("albumName", album);
  formData.append("albumZipFile", albumZipFile);

  const response = await fetch("/albums", {
    method: "POST",
    body: formData,
  });
  return await response.json();
};

export const generateDownloadCodes = async (
  albumId: string,
  numberOfDownloadCodes: number
) => {
  const queryParams = new URLSearchParams({
    numberOfDownloadCodesToGenerate: "" + numberOfDownloadCodes,
  });
  const response = await fetch(
    `/albums/${albumId}/downloadcodes?${queryParams}`,
    {
      method: "POST",
    }
  );
  return response.json();
};
