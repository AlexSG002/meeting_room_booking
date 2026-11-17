import {Component, output, inject, input, ChangeDetectorRef } from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ReservationService} from '../../services/reservation-service';
import {AvailableSlot} from '../../models/available-slot.model';
import {Room} from '../../models/room.model';

@Component({
  imports: [FormsModule],
  selector: 'app-reservation-modal',
  styleUrl: './reservation-modal.css',
  templateUrl: './reservation-modal.html',
})
export class ReservationModal {

  private readonly reservationService =inject(ReservationService);

  rooms = input.required<Room[]>();
  private readonly cdr = inject(ChangeDetectorRef);

  selectedDate = this.formatDate(new Date());
  selectedRoomId = 1;
  availability: AvailableSlot[] = [];
  close = output<void>();

  constructor() {
    this.loadAvailability();
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}-${month}-${day}`;
  }

  loadAvailability(): void {
    this.reservationService
      .getAvailability(this.selectedRoomId, this.selectedDate).subscribe({
      next: (availability) => {
        this.availability = availability.availableSlots;
        this.cdr.detectChanges();
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
