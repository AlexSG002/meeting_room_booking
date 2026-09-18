import {Injectable, inject} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Reservation} from '../models/reservation.model';
import {ReservationRequest} from '../models/reservation-request.model';
import {AvailabilityResponse} from '../models/availability-response.model';

@Injectable({
  providedIn: 'root'
})

export class ReservationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/v1/reservations';

  getAllReservations(){
    return this.http.get<Reservation[]>(this.apiUrl);
  }

  getReservationById(id: number){
    return this.http.get<Reservation[]>(`${this.apiUrl}/${id}`);
  }

  createReservation(reservation: ReservationRequest){
    return this.http.post<Reservation>(this.apiUrl, reservation);
  }

  cancelReservation(id: number){
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAvailability(roomId: number, date: string) {
    return this.http.get<AvailabilityResponse>(
      `${this.apiUrl}/${roomId}/availability`,
      {
        params: {
          date
        }
      }
    );
  }
}

