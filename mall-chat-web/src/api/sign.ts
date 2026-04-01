import { BASE_URL, request } from "./http";

export type SignStatus = {
  signedToday: boolean;
  streak: number;
  total: number;
  year: number;
  month: number;
  day: number;
};

export type SignCheckInResult = SignStatus & {
  firstSign: boolean;
  targetDate: string;
  retro: boolean;
};

export type SignCalendar = {
  year: number;
  month: number;
  daysInMonth: number;
  signedDays: number[];
};

export function fetchSignStatus() {
  return request<SignStatus>(BASE_URL, "/sign/status", { auth: true });
}

export function checkIn() {
  return request<SignCheckInResult>(BASE_URL, "/sign/check-in", { method: "POST", auth: true });
}

export function retroCheckIn(date: string) {
  const query = encodeURIComponent(date);
  return request<SignCheckInResult>(BASE_URL, `/sign/retro?date=${query}`, { method: "POST", auth: true });
}

export function fetchSignCalendar(year: number, month: number) {
  return request<SignCalendar>(BASE_URL, `/sign/calendar?year=${year}&month=${month}`, { auth: true });
}
