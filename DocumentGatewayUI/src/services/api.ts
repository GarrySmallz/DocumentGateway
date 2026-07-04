const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080'

export interface AuditEventDto {
    id: number
    occurredAt: string
    correlationId: string
    partnerId: string | null
    eventType: string
    outcome: string
    errorCode: string | null
}

export interface MessageResponse {
    correlationId: string
    status: string
}

export async function getAuditEvents(apiKey: string, partnerId: string): Promise<AuditEventDto[]> {
    const response = await fetch(`${BASE_URL}/api/v1/audit`, {
        headers: {
            'X-API-Key': apiKey,
            'X-Partner-Id': partnerId
        }
    });
    if (!response.ok) {
        throw new Error(`HTTP ${response.status}`)
    }
    return response.json();
}

export async function sendMessage(
    apiKey: string,
    partnerId: string,
    messageType: string,
    correlationId: string,
    xml: string
): Promise<MessageResponse> {
    const response = await fetch(`${BASE_URL}/api/v1/messages`, {
        method: 'POST',
        headers: {
            'X-API-Key': apiKey,
            'X-Partner-Id': partnerId,
            'X-Message-Type': messageType,
            'X-Correlation-Id': correlationId,
            'Content-Type': 'application/xml'
        },
        body: xml
    })
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    return response.json()
}

export async function getHealth(): Promise<{ status: string }> {
    const response = await fetch(`${BASE_URL}/actuator/health`)
    if (!response.ok) throw new Error(`HTTP ${response.status}`)
    return response.json()
}