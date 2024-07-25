import { ACCESS_TOKEN, REFRESH_TOKEN } from "../service/authentication.service";

export interface AuthenticationResponse {
  [ACCESS_TOKEN]: string,
  [REFRESH_TOKEN]: string,
  scope: string,
  token_type: string,
  expires_in: number
}