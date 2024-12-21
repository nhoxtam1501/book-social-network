import {Component, OnInit} from '@angular/core';
import {BookService} from "../../../../services/services/book.service";
import {Router} from "@angular/router";
import {PageResponseBookResponse} from "../../../../services/models/page-response-book-response";

@Component({
  selector: 'app-book-list',
  templateUrl: './book-list.component.html',
  styleUrls: ['./book-list.component.css']
})
export class BookListComponent implements OnInit {
  page = 0;
  size = 5;
  bookResponse: PageResponseBookResponse = {};
  errorMsg = {};

  constructor(
    private service: BookService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.findAllBooks();
  }

  private findAllBooks() {
    this.service.findAllBooks({
      page: this.page,
      size: this.size
    }).subscribe({
        next: (books) => {
          this.bookResponse = books;
        },
        error: (err) => {
          this.errorMsg = err;
        }
      }
    );
  }

}
