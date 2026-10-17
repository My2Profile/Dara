export interface SessionResponse {
  id: number;
  createdAt: string;
  expiresAt: string;
  userAgent: string;
  ipAddress: string;
  revoked: boolean;
}