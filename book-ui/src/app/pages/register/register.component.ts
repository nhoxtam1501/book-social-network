import {Component} from '@angular/core';
import {Router} from "@angular/router";
import {AuthenticationService} from "../../services/services/authentication.service";
import {ValidationRequest} from "../../services/models/validation-request";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  registerRequest: ValidationRequest = {email: '', firstname: '', lastname: '', password: ''};
  errorMsg: Array<string> = [];

  constructor(
    private route: Router,
    private service: AuthenticationService
  ) {
  }

  login() {
    this.route.navigate(["login"]);
  }

  register() {
    this.errorMsg = [];
    this.service.register({
      body: this.registerRequest,
    }).subscribe({
      next: (response) => {
        this.route.navigate(['activate-account']);
      },
      error: (err) => {
        console.log(err);
        if (err.error.validationErrors) {
          this.errorMsg = err.error.validationErrors;
        } else {
          this.errorMsg.push(err.error.error);
        }
      }
    })
    ;
  }

}
