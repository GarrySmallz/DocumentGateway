import { Badge } from '@/components/ui/badge'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import {useEffect, useState} from "react";
import {getHealth} from "@/services/api.ts";

export function SystemOverview() {
    const [status, setStatus] = useState<string | null>(null)
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        getHealth()
            .then((data) => setStatus(data.status))
            .catch(() => setStatus('DOWN'))
            .finally(() => setLoading(false))
    }, [])

    return (
        <Card>
            <CardHeader>
                <CardTitle>System Status</CardTitle>
            </CardHeader>
            <CardContent>
                {loading ? (
                    <span>Lädt...</span>
                ) : (
                    <Badge variant={status === 'UP' ? 'default' : 'destructive'}>
                        {status}
                    </Badge>
                )}
            </CardContent>
        </Card>
    )
}