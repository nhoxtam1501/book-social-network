import {CanActivateFn} from '@angular/router';

export const authGuard: CanActivateFn = () => true;  //= () => {
//   const tokenService = inject(KeycloakService);
//   const router = inject(Router);
//   if (router.url === '/login') {
//     console.log('User is already on login page.');
//     return false; // Deny further navigation
//   }
//   if (tokenService.keycloak.isTokenExpired()) {
//     console.log("Token expired. Redirecting to /login")
//     router.navigate(['login']);
//     return false;
//   }
//   return true;
// };
