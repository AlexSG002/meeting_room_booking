import { AvailableSlot } from './available-slot.model';

export interface AvailabilityResponse {
  roomId: number;
  date: string;
  availableSlots: AvailableSlot[];
}
