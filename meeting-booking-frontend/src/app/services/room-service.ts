import {inject, Injectable, Service} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Room} from '../models/room.model';

@Injectable({
  providedIn: 'root'
})
export class RoomService {

  private readonly http = inject(HttpClient);

  private readonly apiUrl = 'http://localhost:8080/api/v1/rooms';

  getAllRooms() {
    return this.http.get<Room[]>(this.apiUrl);
  }
}
