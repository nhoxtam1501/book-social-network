import {Injectable} from '@angular/core';
import Keycloak from "keycloak-js/lib/keycloak";

export default Keycloak;

@Injectable({
  providedIn: 'root'
})
export class AppKeycloakService {
  private _keycloak: Keycloak | undefined

  get keycloak() {
    if (!this._keycloak) {
      this._keycloak = new Keycloak({
          url: 'http://localhost:9090',
          realm: 'book-social-network',
          clientId: 'bsn'
        }
      )
    }
    return this._keycloak
  }

  constructor() {
  }

  async init() {
    console.log('Authenticating user...');
    const authenticated = await this.keycloak?.init({
        onLoad: 'login-required',
      }
    )
    if (authenticated) {
      console.log('User authenticated');
    }
  }
}
