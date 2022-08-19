import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {

  constructor(private httpClient : HttpClient) {}

  publicationDate: any;
  headerImageURL: any;
  itemsList: any;

  ngOnInit(): void{
    this.httpClient.get("http://localhost:8080/").subscribe(data => {
      var nyt_data = JSON.parse(JSON.stringify(data));
      this.itemsList = nyt_data.item;
      this.headerImageURL =  nyt_data.headerImageURL;
      this.publicationDate = nyt_data.pubDate;
      console.log("nyt_data", nyt_data.item)
      console.log("headerImageURL", nyt_data.headerImageURL);
      console.log("pubDate", nyt_data.pubDate);
    });
  }
}
