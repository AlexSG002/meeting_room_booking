import {Component, output, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ReservationService} from '../../services/reservation-service';
import {AvailableSlot} from '../../models/available-slot.model';

@Component({
  imports: [FormsModule],
  selector: 'app-reservation-modal',
  styleUrl: './reservation-modal.css',
  templateUrl: './reservation-modal.html',
})
export class ReservationModal {

  private readonly reservationService =inject(ReservationService);

  selectedDate = '2026-09-18';
  selectedRoomId = 1;
  availability: AvailableSlot[] = [];
  close = output<void>();

  constructor() {
    this.loadAvailability();
  }

  loadAvailability(): void {
    this.reservationService
      .getAvailability(this.selectedRoomId, this.selectedDate).subscribe({
      next: (availability) => {
        this.availability = availability.availableSlots;
        console.log('Availability', availability);
      },
      error: (error) => {
        console.error('Error loading availability:', error);
      }
    });
  }



  closeReservationModal(){
    this.close.emit();
  }

}
